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

import ueditor.Uploader

import javax.servlet.ServletContext
import javax.servlet.http.HttpServletRequest

class UeditorHandlerController {
    static String[] IMAGE_FILE_TYPES = [".gif" , ".png" , ".jpg" , ".jpeg" , ".bmp"] as String[];
    static String[] FILE_TYPES = [".rar", ".doc" , ".docx" , ".zip" , ".pdf" , ".txt" , ".swf", ".wmv"] as String[];  //允许的文件类型

    def ueditorConfigService

    def beforeInterceptor = {
        println "Tracing action ${actionUri}"
    }

    def index() {
        render 'OK'
    }

    def imageUp() {
        Uploader up = new Uploader(request);
        up.setSavePath(ueditorConfigService.getUploadFolder('image'));
        up.setAllowFiles(IMAGE_FILE_TYPES);
        up.setMaxSize(10000); //单位KB
        up.upload();
        response.getWriter().print("{'original':'"+up.getOriginalName()+"','url':'"+up.getUrl()+"','title':'"+up.getTitle()+"','state':'"+up.getState()+"'}");
    }

    def wordImageUp() {
        Uploader up = new Uploader(request);
        up.setSavePath(ueditorConfigService.getUploadFolder('wordImage'));
        up.setAllowFiles(IMAGE_FILE_TYPES);
        up.setMaxSize(10000); //单位KB
        up.upload();
        response.getWriter().print("{'original':'"+up.getOriginalName()+"','url':'"+up.getUrl()+"','title':'"+up.getTitle()+"','state':'"+up.getState()+"'}");
    }

    def scrawlUp() {
        String param = request.getParameter("action");
        Uploader up = new Uploader(request);
        up.setSavePath(ueditorConfigService.getUploadFolder('scrawl'));
        up.setAllowFiles(IMAGE_FILE_TYPES);
        up.setMaxSize(10000); //单位KB

        if(param!=null && param.equals("tmpImg")){
            up.upload();
            response.getWriter().print("<script>parent.ue_callback('" + up.getUrl() + "','" + up.getState() + "')</script>");
        } else {
            up.uploadBase64("content");
            response.getWriter().print("{'url':'" + up.getUrl()+"',state:'"+up.getState()+"'}");
        }
    }

    def fileUp() {
        Uploader up = new Uploader(request);
        up.setSavePath(ueditorConfigService.getUploadFolder('file')); //保存路径
        up.setAllowFiles(FILE_TYPES);
        up.setMaxSize(10000);        //允许的文件最大尺寸，单位KB
        up.upload();
        response.getWriter().print("{'url':'"+up.getUrl()+"','fileType':'"+up.getType()+"','state':'"+up.getState()+"','original':'"+up.getOriginalName()+"'}");
    }

    def getRemoteImage(String upfile) {
        String state = "远程图片抓取成功！";

        String filePath = ueditorConfigService.getUploadFolder('catcher');
        String[] arr = upfile.split("ue_separate_ue");
        String[] outSrc = new String[arr.length];
        for(int i=0;i<arr.length;i++){
            //保存文件路径
            String savePath = getPhysicalPath(request, filePath);
            //格式验证
            String type = getFileType(arr[i]);
            if(type.equals("")){
                state = "图片类型不正确！";
                continue;
            }
            String saveName = Long.toString(new Date().getTime())+type;
            //大小验证
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection conn = (HttpURLConnection) new URL(arr[i]).openConnection();
            if(conn.getContentType().indexOf("image")==-1){
                state = "请求地址头不正确";
                continue;
            }
            if(conn.getResponseCode() != 200){
                state = "请求地址不存在！";
                continue;
            }
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File savetoFile = new File(savePath +"/"+ saveName);
            outSrc[i]=filePath +"/"+ saveName;
            try {
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(savetoFile);
                int b;
                while ((b = is.read()) != -1) {
                    os.write(b);
                }
                os.close();
                is.close();
                // 这里处理 inputStream
            } catch (Exception e) {
                log.error(e.message, e);
            }
        }
        String outstr = "";
        for(int i=0;i<outSrc.length;i++){
            outstr += outSrc[i]+"ue_separate_ue";
        }
        outstr = outstr.substring(0,outstr.lastIndexOf("ue_separate_ue"));
        response.getWriter().print("{'url':'" + outstr + "','tip':'"+state+"','srcUrl':'" + upfile + "'}" );
    }

    def imageManager() {
        String path = ueditorConfigService.getUploadFolder('imageManager');
        String imgStr ="";
        String realpath = getRealPath(request, path)+"/"+path;
        List<File> files = getFiles(realpath,new ArrayList());
        for(File file :files ){
            imgStr+=file.getPath().replace(getRealPath(request,path),"")+"ue_separate_ue";
        }
        if(imgStr!=""){
            imgStr = imgStr.substring(0,imgStr.lastIndexOf("ue_separate_ue")).replace(File.separator, "/").trim();
        }
        response.getWriter().print(imgStr);
    }

    def getMovie(String searchKey, String videotype) {
        request.setCharacterEncoding("utf-8");
        response.setCharacterEncoding("utf-8");
        StringBuffer readOneLineBuff = new StringBuffer();
        String content ="";
        try {
            searchKey = URLEncoder.encode(searchKey,"utf-8");
            URL url = new URL("http://api.tudou.com/v3/gw?method=item.search&appKey=myKey&format=json&kw="+ searchKey+"&pageNo=1&pageSize=20&channelId="+videotype+"&inDays=7&media=v&sort=s");
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                readOneLineBuff.append(line);
            }
            content = readOneLineBuff.toString();
            reader.close();
        } catch (MalformedURLException e) {
            log.error(e.message, e);
        } catch (IOException e2) {
            log.error(e2.message, e2);
        }
        response.getWriter().print(content);
    }

    private static String getFileType(String fileName){
        Iterator<String> type = IMAGE_FILE_TYPES.iterator();
        while(type.hasNext()){
            String t = type.next();
            if(fileName.endsWith(t)){
                return t;
            }
        }
        return "";
    }

    private static String getPhysicalPath(def request, String path) {
        String servletPath = request.getServletPath();
        String realPath = request.getSession().getServletContext().getRealPath(servletPath);
        return new File(realPath).getParent() +"/" +path;
    }

    private List getFiles(String realpath, List files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for(File file :subfiles ){
                if(file.isDirectory()){
                    getFiles(file.getAbsolutePath(),files);
                }else{
                    if(!getFileType(file.getName()).equals("")) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    private static String getRealPath(HttpServletRequest request, String path){
        ServletContext application = request.getSession().getServletContext();
        String str = application.getRealPath(request.getServletPath());
        return new File(str).getParent();
    }
}
