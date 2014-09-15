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
    def pluginManager
    def ueditorConfigService

    def resources = { attrs ->
        def minified = false
        if (Environment.current == Environment.PRODUCTION) {
            minified = attrs?.minified ? attrs?.minified == 'true' : true
        }
        attrs.remove('minified')
        def editor = new Ueditor(grailsApplication, getPluginResourcePath(request), getPluginVersion(), attrs.remove('lang'))
        out << editor.renderResources(g, minified)
    }
 
    def config = { attrs, body ->
        def cfg = new UeditorConfig(grailsApplication)
        def target = attrs.remove('target')
        def var = attrs.remove('var')
        try {
            if (var) {
                def value = body()
                cfg.addConfigItem(var, value)
            } else {
                cfg.addConfigItems(attrs)
            }
        } catch (Exception e) {
            throwTagError(e.message)
        }
        out << cfg.renderConfigurations(target)
    }

    def editor = { attrs, body ->
        if (!attrs.id) throwTagError("Tag [editor] is missing required attribute [id]")
        String id = attrs.id
        String value = attrs.value ?: body()
        def editor = new Ueditor(grailsApplication, getPluginResourcePath(request), getPluginVersion())
        out << editor.renderEditor(id, value, attrs)
    }

    private String getPluginResourcePath(def request) {
        return "${request.contextPath}/plugins/${UeditorConfig.PLUGIN_NAME.toLowerCase()}-$pluginVersion"
    }

    private String getPluginVersion() {
        return pluginManager.getGrailsPlugin(UeditorConfig.PLUGIN_NAME)?.version
    }
}
