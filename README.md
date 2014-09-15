grails-ueditor
==============

Grails Ueditor Plugin for Baidu UEditor(http://ueditor.baidu.com/)


## Installation

To install the plugin add a dependency to BuildConfig.groovy:
~~~~~~~~~~~
compile ":ueditor:1.4.3"
~~~~~~~~~~~

## Configuration



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

