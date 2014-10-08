/**
 Copyright Hai-Hua Xiao

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package org.grails.plugin.ueditor

import com.baidu.ueditor.hunter.FileManager
import grails.converters.JSON

class UeditorHandlerController {
    static String[] IMAGE_FILE_TYPES = [".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"] as String[];
    static String[] FILE_TYPES = [".rar", ".doc" , ".docx" , ".zip" , ".pdf" , ".txt" , ".swf", ".wmv"] as String[];  //允许的文件类型

    def ueditorConfigService

    def index() {
        response.sendError(HttpURLConnection.HTTP_OK)
    }

    def handle(String userSpace) {
        def action = request.getParameter('action')
        if('config' == action) {
            forward(action: 'config')
        } else if(action.startsWith('upload')) {
            String type = action.substring(6)
            forward(action: 'upload', params: [xtype: type, userSpace: userSpace])
        } else if(action.startsWith('list')) {
            String type = action.substring(4)
            forward(action: 'list', params: [xtype: type, userSpace: userSpace])
        } else {
            response.sendError(HttpURLConnection.HTTP_NOT_FOUND)
        }
    }

    def config() {
        render ueditorConfigService.config as JSON
    }

    def download(String type, String path) {
        Uploader up = new Uploader()
        up.setSavePath(ueditorConfigService.getUploadFolder(type))

        File file = new File(up.getPhysicalPath(request, path))
        if(file.file && file.exists()) {
            FileInputStream fis = new FileInputStream(file)
            response.outputStream << fis
        } else {
            response.sendError(HttpURLConnection.HTTP_NOT_FOUND)
        }
    }

    def upload(String xtype, String userSpace) {
        Uploader up = new Uploader()
        up.setSavePath(ueditorConfigService.getUploadFolder(xtype))
        up.upload(request, userSpace)

        String callback = request.getParameter("callback");

        String message = g.message(code: "ueditor.errorinfo.${up.state.name()}", default: '')

        String result = "{\"name\":\""+ up.getFileName() +"\", \"originalName\": \""+ up.getOriginalName() +"\", \"size\": "+ up.size +", \"state\": \""+ message +"\", \"type\": \""+ up.getType() +"\", \"url\": \""+ up.getUrl() +"\"}";

        result = result.replaceAll( "\\\\", "\\\\" );

        if( callback == null ){
            render ( result )
        } else{
            render ("<script>"+ callback +"(" + result + ")</script>")
        }
    }

    def list(String xtype, String userSpace, int start, Integer count) {
        Uploader up = new Uploader();
        up.setSavePath(ueditorConfigService.getUploadFolder(xtype))
        String rootPath = up.getPhysicalPath(request, '')

        Map conf = [
            rootPath: rootPath,
            dir: userSpace ?: '',
            count: count ?: 20,
            allowFiles: ueditorConfigService.config."${xtype}ManagerAllowFiles" as String[]
        ]
        FileManager fm = new FileManager( conf )
        render( text: fm.listFile(start).toJSONString(), contentType: "application/json", encoding: "UTF-8" )
    }
}
