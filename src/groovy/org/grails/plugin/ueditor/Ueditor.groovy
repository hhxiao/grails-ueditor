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

class Ueditor {
    def config
    def basePath
    def version
    def lang
    def urlHandlers

    Ueditor(def grailsApplication, String basePath, String version) {
        this(grailsApplication, basePath, version, '')
    }

    Ueditor(def grailsApplication, String basePath, String version, String lang) {
        this.config = new UeditorConfig(grailsApplication)
        this.basePath = basePath
        this.version = version
        this.lang = lang ?: 'en'
    }

    def renderResources(g, minified) {
        def ueditorHome = "${basePath}/ueditor-${version}"
        return """
    <script type="text/javascript">
        window.UEDITOR_HOME_URL = "${ueditorHome}/";
        window.UEDITOR = {config:{default:{}},instance:{}};
    </script>
    <script type="text/javascript" src="${ueditorHome}/ueditor.config.js"></script>
    <script type="text/javascript" src="${ueditorHome}/ueditor.all${minified ? '.min' : ''}.js"></script>
    <script type="text/javascript" src="${ueditorHome}/lang/${lang}/${lang}.js"></script>
    <script type="text/javascript">
        window.UEDITOR_CONFIG.serverUrl = "${g.createLink(controller: 'ueditorHandler', action: 'handle')}";
    </script>
"""
    }

    def renderEditor(String instanceId, String instanceName, String initialValue, def attrs) {
        StringBuilder buf = new StringBuilder()
        if (this.config.append) {
            buf << """
    <textarea ${instanceId?'id="'+instanceId+'"':''} ${instanceName?'name="'+instanceName+'"':''} ${attrs.collect {it.key + '="' + it.value + '"'}.join(' ')}>${initialValue?.encodeAsHTML()}</textarea>"""
        }
        buf << """
    <script type="text/javascript">
        UEDITOR.instance.${instanceId} = new UE.ui.Editor(UEDITOR.config.${instanceId} || UEDITOR.config.default);
        UEDITOR.instance.${instanceId}.render("${instanceId}");
    </script>"""
        return buf.toString()
    }
}
