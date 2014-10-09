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

import grails.util.Environment

class UeditorTagLib {
    static namespace = "ueditor"

    def grailsApplication
    def ueditorConfigService

    def resources = { attrs ->
        def minified = false
        if (Environment.current == Environment.PRODUCTION) {
            minified = attrs?.minified ? attrs?.minified == 'true' : true
        }
        def editor = ueditorConfigService.newEditor(request)
        String lang = attrs.lang ?: ueditorConfigService.resolveLang(request)
        def customerConfigJs = ueditorConfigService.customConfig
        out << editor.renderResources(g, minified, lang, customerConfigJs ? "${request.contextPath}${customerConfigJs}" : "", attrs.userSpace)
    }
 
    def config = { attrs, body ->
        def target = attrs.remove('target')
        if (!target) throwTagError("Tag [config] is missing required attribute [target]")

        def cfg = new UeditorConfig(grailsApplication)
        def var = attrs.remove('var')
        try {
            if (var) {
                def value = attrs.value ?: body()
                cfg.addConfigItem(g, var, value)
            }
            cfg.addConfigItems(g, attrs)
        } catch (Exception e) {
            throwTagError(e.message)
        }
        out << cfg.renderConfigurations(target)
    }

    def editor = { attrs, body ->
        if (!attrs.id && !attrs.name) throwTagError("Tag [editor] is missing required attribute [id|name]")

        String value = attrs.value ?: body()
        def editor = ueditorConfigService.newEditor(request)
        String id = attrs.remove('id')
        if(!id) id = attrs.name
        out << editor.renderEditor(id, value, attrs)
    }
}
