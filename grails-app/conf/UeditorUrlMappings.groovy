class UeditorUrlMappings {

	static mappings = {
        "/ueditorHandler/file/$type/$path**?"(controller: "ueditorHandler") {
            action = [GET: 'download']
        }

        "/ueditorHandler/$action"(controller: "ueditorHandler") {
        }
	}
}
