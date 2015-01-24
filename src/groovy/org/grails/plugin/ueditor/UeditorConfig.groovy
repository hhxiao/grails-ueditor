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

    def grailsApplication
    def skipAllowedItemsCheck
    def append

    def config = [:]

    UeditorConfig(def grailsApplication) {
        this.grailsApplication = grailsApplication
        def cfg = grailsApplication.config
        this.skipAllowedItemsCheck = cfg.ueditor?.skipAllowedItemsCheck ?: false
        this.append = cfg.ueditor?.append ?: true
    }

    def addConfigItems(def g, def attrs) {
        attrs?.each { key, value ->
            addConfigItem(g, key, value)
        }
    }

    def addConfigItem(def g, def key, def value) {
        if(!skipAllowedItemsCheck && !ALLOWED_CONFIG_ITEMS.contains(key)) {
            throw new IllegalArgumentException("Invalid config item: $key")
        }
        if(key == 'toolbars') {
            def buttonDef = g.message(code: "ueditor.toolbar.${value}".toString(), default: value)
            // convert 2 dim array
            value = buttonDef.split(',').collect { it.split(' ').collect { "'${it.trim()}'" } }
        }
        this.config[key] = value
    }

    String getConfiguration() {
        def configs = []

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
        return """
    <script type="text/javascript">
        ${target ? "UEDITOR.config.${target}" : "UEDITOR.config.defaults"} = ${getConfiguration()};
    </script>
"""
    }

    static final ALLOWED_CONFIG_ITEMS = [
            'serverUrl',
            'labelMap',
            'lang',
            'langPath',
            'theme',
            'themePath',
            'zIndex',
            'charset',
            'customDomain',
            'isShow',
            'textarea',
            'initialContent',
            'initialFrameWidth',
            'initialFrameHeight',
            'autoClearinitialContent',
            'autoClearEmptyNode',
            'focus',
            'initialStyle',
            'iframeCssUrl',
            'indentValue',
            'initialFrameWidth',
            'initialFrameHeight',
            'fullscreen',
            'readonly',
            'zIndex',
            'autoClearEmptyNode',
            'enableAutoSave',
            'saveInterval',
            'imageScaleEnabled',
            'imagePopup',
            'autoSyncData',
            'emotionLocalization',
            'retainOnlyLabelPasted',
            'pasteplain',
            'filterTxtRules',
            'allHtmlEnabled',
            'insertorderedlist',
            'insertunorderedlist',
            'listDefaultPaddingLeft',
            'listiconpath',
            'maxListLevel',
            'autoTransWordToList',
            'fontfamily',
            'fontsize',
            'paragraph',
            'rowspacingtop',
            'rowspacingbottom',
            'lineheight',
            'customstyle',
            'enableContextMenu',
            'contextMenu',
            'shortcutMenu',
            'elementPathEnabled',
            'wordCount',
            'maximumWords',
            'wordCountMsg',
            'wordOverFlowMsg',
            'tabSize',
            'tabNode',
            'removeFormatTags',
            'removeFormatAttributes',
            'maxUndoCount',
            'maxInputCount',
            'autoHeightEnabled',
            'scaleEnabled',
            'minFrameWidth',
            'minFrameHeight',
            'autoFloatEnabled',
            'topOffset',
            'toolbarTopOffset',
            'pageBreakTag',
            'autotypeset',
            'tableDragable',
            'disabledTableInTable',
            'sourceEditor',
            'codeMirrorJsUrl',
            'codeMirrorCssUrl',
            'sourceEditorFirst',
            'iframeUrlMap',
            'webAppKey',
            'allowDivTransToP',
            'toolbars'
    ]
}
