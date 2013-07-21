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

class UeditorConfigService {
    def grailsApplication

    boolean isUploadEnabled(String type) {
        def path = grailsApplication.config.ueditor?.uploader?."${type}"?.path?.toString()
        return path ? true : false
    }

    String getUploadFolder(String type) {
        return grailsApplication.config.ueditor?.uploader?."${type}"?.path?.toString()
    }

    def getUrlHandlers(def g, def request) throws Exception {
        def urlConfigurations = [:]

        ['image':'imageUp', 'scrawl':'scrawlUp', 'file':'fileUp',
                'catcher':'getRemoteImage', 'imageManager':'imageManager',
                'wordImage':'wordImageUp'].each { type, action ->
            boolean enabled = isUploadEnabled(type)
            if(enabled) {
                def url = g.createLink(controller: 'ueditorUrlHandler', action: action)
                def path = g.createLink(controller: 'ueditorUrlHandler', action: 'index')
                urlConfigurations.put("${type}Url".toString(), "\"${url}\"".toString())
                urlConfigurations.put("${type}Path".toString(), "\"${path}\"".toString())
            }
        }
        return urlConfigurations
    }
}
