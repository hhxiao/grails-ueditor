[![Build Status](https://travis-ci.org/hhxiao/grails-ueditor.svg)](https://travis-ci.org/hhxiao/grails-ueditor)

grails-ueditor
==============

Grails Ueditor Plugin for Baidu UEditor(http://ueditor.baidu.com/)


## Installation

To install the plugin add a dependency to BuildConfig.groovy:
~~~~~~~~~~~
compile ":ueditor:1.4.3"
~~~~~~~~~~~

## Configuration

Configure toolbar buttions, use predefined toolbar types: mini, compcat, default, full.

~~~~~~~~~~~
<ueditor:config var="toolbars" value="compact"/>
~~~~~~~~~~~

Or customize it, 

~~~~~~~~~~~
<ueditor:config var="toolbars">
    fullscreen source | undo redo | bold italic underline fontborder strikethrough superscript subscript blockquote pasteplain | forecolor backcolor insertorderedlist insertunorderedlist
</ueditor:toolbar>
~~~~~~~~~~~

More configuration items in Config.groovy
~~~~~~~~~~~
ueditor {
    config = '/assets/js/custom.config.js'  // Ueditor's custome config file, relative to the webapp root
    upload = [
        baseUrl: 'http://localhost/',  // Use another webserver to serve the file download request
        baseDir: '/var/lib/ueditor/app' // Save to the folders relative to the baseDir instead of the default value  "grails/ueditorHandler "
    ]
}
~~~~~~~~~~~


## Usage

Include required resources in page header

~~~~~~~~~~~
<r:require module="jquery"/>
<ueditor:resources/>
~~~~~~~~~~~

Declare editor in form
~~~~~~~~~~~
<g:form>
    <ueditor:editor id="body" style="width:100%;height:360px;">Hello World</ueditor:editor>
</g:form>
~~~~~~~~~~~

