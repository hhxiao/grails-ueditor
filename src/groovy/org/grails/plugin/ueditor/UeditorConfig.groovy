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

class UeditorConfig {
    static final PLUGIN_NAME = "ueditor"
    static final DEFAULT_BASEDIR = "/uploads/"

    def grailsApplication
    def skipAllowedItemsCheck
    def append

    def config = [:]

    UeditorConfig(grailsApplication) {
        this.grailsApplication = grailsApplication
        def cfg = grailsApplication.config
        this.skipAllowedItemsCheck = cfg.editor?.skipAllowedItemsCheck ?: false
        this.append = cfg.editor?.append ?: true
    }

    def addConfigItems(attrs) {
        attrs?.each { key, value ->
            this.config[key] = value
        }
    }

    def addConfigItem(key, value) {
        this.config[key] = value
    }

    String getConfiguration() {
        def configs = []

        def uconfig = grailsApplication.config.ueditor?.config
        if(uconfig) configs.add(uconfig)
        this.config.each {k, v ->
            if(v != null) configs << "${k}: ${v instanceof String ? "\"$v\"" : v}"
        }
        StringBuilder cfg = new StringBuilder()
        if (configs.size()) {
            cfg << """{\n"""
            cfg << "\t\t" << configs.join(",\n\t\t")
            cfg << """}"""
        }
        return cfg.toString()
    }

    def renderConfigurations(def target) {
        return """<script type="text/javascript">
    ${target ? "UEDITOR.config.${target}" : "UEDITOR.config.default"} = ${getConfiguration()};
</script>
"""
    }

    static final ALLOWED_CONFIG_ITEMS = [
            'labelMap',
            'webAppKey',
            'lang',
            'theme',
            'customDomain',
            'charset',
            'isShow',
            'initialContent',
            'initialFrameWidth',
            'initialFrameHeight',
            'autoClearinitialContent',
            'autoClearEmptyNode',
            'fullscreen',
            'readonly',
            'zIndex',
            'fontsize',
            'paragraph',
            'rowspacingtop',
            'rowspacingBottom',
            'lineheight',
            'contextMenu',
            'focus',
            'autoFloatEnabled',
            'wordCount',
            'maximumWords',
            'wordCountMsg',
            'indentValue',
            'pageBreakTag',
            'wordCount',
            'emotionLocalization',
            'autoClearinitialContent',
            'autoHeightEnabled',
            'elementPathEnabled',
            'imagePopup',
            'pasteplain',
            'sourceEditor',
            'contextMenu'
    ]
}