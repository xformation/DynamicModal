# DynamicModal

Application to create new entity model class from json configuration and json itself.




### /dynModel/create

Method to create data models using config objects.

Method: POST
Parameters: config  
{
	"className": {
	"cloudName": "AWS, AZure, Kubernete ...",
	"groupName": "Networking, Cloud ...",
	"fields": [{
		"key": "key",
		"type": "String|Text|Integer|Long|Double|Date|Boolean|Object",
		"default": "value",
		"isArray": true|false,
		"clsName": "",
		"isNullable": true|false,
		"length": n,
		"validations": true|false,
		"isRequire": true|false,
		"min": n,
		"max": n,
		"regex": "^$",
		"regexMsg": "Regex validation failed",
		"dateFromat": "dd-MM-YYYY",
		"isCompositeKeyMem": true|false
	}]
 }
}
 
Returns: Json of pojo model or exception.

