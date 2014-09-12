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

import org.springframework.beans.factory.InitializingBean

class UeditorConfigService implements InitializingBean {
    def uploadFolders = [:]
    def grailsApplication

    String getUploadFolder(String type) {
        uploadFolders.get(type)
    }

    def getUrlHandlers(def g, def request) throws Exception {
        def urlConfigurations = [:]

        typeActionMap.each { type, action ->
            def url = g.createLink(controller: 'ueditorUrlHandler', action: action)
            def path = g.createLink(controller: 'ueditorUrlHandler', action: 'index')
            urlConfigurations.put("${type}Url".toString(), "\"${url}\"".toString())
            urlConfigurations.put("${type}Path".toString(), "\"${path}\"".toString())
        }
        return urlConfigurations
    }

    @Override
    void afterPropertiesSet() throws Exception {
        typeActionMap.each { type, action ->
            def path = grailsApplication.config.ueditor?.uploader?."${type}"?.path
            if(!path) path = "upload/${type}"
            File folder = new File(path.toString())
            folder.mkdirs()
            uploadFolders.put(type, folder.absolutePath)
        }
    }

    static def typeActionMap = ['image':'imageUp', 'scrawl':'scrawlUp', 'file':'fileUp',
            'catcher':'getRemoteImage', 'imageManager':'imageManager',
            'wordImage':'wordImageUp']
}
