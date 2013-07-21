class UrlMappings {

	static mappings = {
        "/ueditor/"(controller: "ueditorHandler", action: 'index') {
        }

        "/ueditor/$action"(controller: "ueditorHandler") {
        }

		"/$controller/$action?/$id?"{
			constraints {
				// apply constraints here
			}
		}

		"/"(view:"/index")
		"500"(view:'/error')
	}
}
