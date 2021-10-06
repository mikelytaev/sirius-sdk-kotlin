package com.sirius.library.agent.connections

class RemoteCallWrapper2(var rpc: AgentRPC) : RemoteCall {
    var response: Any? = null

    /*   public Object  serializeResponse(Object... objects) {
        if(response!=null){
            System.out.println("serializeResponse="+response.getClass());
        }
        if (response instanceof JSONObject) {
            return ((JSONObject) response).toString();
        }else if(response instanceof JSONArray){
            List<Object> objectList = new ArrayList<>();
            for(int i=0;i<((JSONArray) response).length();i++){
                objectList.add(serializeResponseTo(((JSONArray) response).get(i),object));
            }
            return objectList;
        }else if(response instanceof Pair && objects.length==2){
            Object firstObject =  serializeResponse(((Pair) response).first);
            Object secondObject =  serializeResponseTo(((Pair) response).second);
            return   new Pair(firstObject,secondObject);
        }

        return  object;
    }*/
    fun serializeResponseTo(response: Any, `object`: Any?): Any? {
        if (`object` != null) {
            println("serializeResponse=" + response.javaClass)
        }
        if (response is JSONObject) {
            return (response as JSONObject).toString()
        } else if (response is JSONArray) {
            val objectList: MutableList<Any?> = ArrayList<Any>()
            for (i in 0 until (response as JSONArray).length()) {
                objectList.add(serializeResponseTo((response as JSONArray).get(i), `object`))
            }
            return objectList
        } else if (response is Pair) {
            val firstObject = serializeResponseTo(response.first!!, `object`)
            val secondObject = serializeResponseTo(response.second!!, `object`)
            return Pair(firstObject, secondObject)
        }
        return `object`
    }

    fun remoteCall(type: String?, params: RemoteParamsBuilder): RemoteCallWrapper2? {
        try {
            response = rpc.remoteCall(type, params.build())
            return this
        } catch (siriusConnectionClosed: java.lang.Exception) {
            siriusConnectionClosed.printStackTrace()
        }
        return null
    }
}
