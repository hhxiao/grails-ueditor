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

class UeditorConfigService implements InitializingBean {
    def grailsApplication
    String serverUrl
    JSONObject config

    String getUploadFolder(String type) {
        type
    }

    String getUrlPrefix(String type) {
        "${serverUrl}/ueditorHandler/file/${type}/"
    }

    @Override
    void afterPropertiesSet() throws Exception {
        serverUrl = grailsApplication.config.grails.serverURL.toString()

        def text = new ClassPathResource('/config.json').inputStream.text
        config = JSON.parse(text)
        config.entrySet().each {
            String key = it.key
            if(key.endsWith('UrlPrefix')) {
                String type = key.substring(0, key.indexOf('UrlPrefix'))
                config.put(key, getUrlPrefix(type))
            }
        }
    }
}
