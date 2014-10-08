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

import grails.converters.JSON
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.beans.factory.InitializingBean
import org.springframework.core.io.ClassPathResource
import org.springframework.web.context.support.ServletContextResource

import javax.servlet.http.HttpServletRequest
import java.util.concurrent.ConcurrentHashMap

class UeditorConfigService implements InitializingBean {
    def grailsApplication
    def pluginManager

    String serverUrl
    JSONObject config
    ConfigObject pluginConfig

    private Map<String, String> supportedLang = new ConcurrentHashMap<>()

    private pluginVersion
    private ueditorVersion

    String getUploadFolder(String type) {
        type
    }

    String getUrlPrefix(String type) {
        "${serverUrl}/ueditorHandler/file/${type}/"
    }

    String resolveLang(HttpServletRequest request) {
        String tag = request.locale.toLanguageTag().toLowerCase()
        String lang = supportedLang.get(tag)
        if(lang) return lang

        String path = ueditorResourcePath + '/lang/' + tag + '/' + tag + '.js'
        ServletContextResource scr = new ServletContextResource(request.servletContext, path)
        lang = scr.exists() ? tag : 'en'
        supportedLang.put(tag, lang)
        return lang
    }

    Ueditor newEditor(def request) {
        new Ueditor(grailsApplication, "${request.contextPath}/${ueditorResourcePath}")
    }

    String getUeditorResourcePath() {
        return "/plugins/${UeditorConfig.PLUGIN_NAME.toLowerCase()}-$pluginVersion/${UeditorConfig.PLUGIN_NAME.toLowerCase()}-$ueditorVersion"
    }

    @Override
    void afterPropertiesSet() throws Exception {
        pluginVersion = pluginManager.getGrailsPlugin(UeditorConfig.PLUGIN_NAME)?.version

        // with this new version scheme, version 1.4.3_2 is a plugin patch to ueditor 1.4.3
        if(pluginVersion.contains('_')) {
            ueditorVersion = pluginVersion.substring(0, pluginVersion.indexOf('_'))
        } else {
            ueditorVersion = pluginVersion
        }

        serverUrl = grailsApplication.config.grails.serverURL.toString()
        config = (JSONObject)JSON.parse(new ClassPathResource('/UeditorConfig.json').inputStream.text)
        config.entrySet().each {
            String key = it.key
            if(key.endsWith('UrlPrefix')) {
                String type = key.substring(0, key.indexOf('UrlPrefix'))
                config.put(key, getUrlPrefix(type))
            }
        }
        pluginConfig = grailsApplication.config.grails.ueditor.config
    }
}
