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
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

class UeditorConfigService implements InitializingBean {
    def grailsApplication
    def pluginManager

    String contextPath
    JSONObject config

    private Map<String, String> supportedLang = new ConcurrentHashMap<>()

    private String pluginVersion
    private String ueditorVersion
    private def userConfig

    String getUploadFolder(String type) {
        def baseDir = userConfig.upload.baseDir
        if(baseDir) {
            "${baseDir}/${type}"
        } else {
            type
        }
    }

    String getCustomConfig() {
        userConfig.config ? userConfig.config.toString() : ''
    }

    String getUrlPrefix(String type) {
        def baseUrl = userConfig.upload.baseUrl
        if(type.endsWith('Manager')) type = type.substring(0, type.length() - 7)
        if(baseUrl) {
            "${baseUrl}/${type}/"
        } else {
            "${contextPath}/ueditorHandler/file/${type}/"
        }
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
        new Ueditor(grailsApplication, "${request.contextPath}${ueditorResourcePath}")
    }

    String getUeditorResourcePath() {
        return "/plugins/${UeditorConfig.PLUGIN_NAME}-$pluginVersion/${UeditorConfig.PLUGIN_NAME}-$ueditorVersion"
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

        contextPath = grailsApplication.mainContext.servletContext.contextPath

        userConfig = grailsApplication.config.ueditor

        config = (JSONObject)JSON.parse(new ClassPathResource('/UeditorConfig.json').inputStream, StandardCharsets.UTF_8.name())
        config.entrySet().each {
            String key = it.key
            if(key.endsWith('UrlPrefix')) {
                String type = key.substring(0, key.indexOf('UrlPrefix'))
                config.put(key, getUrlPrefix(type))
            }
        }
        println getCustomConfig()
        println userConfig.upload.baseUrl
        println userConfig.upload._baseUrl
    }
}
