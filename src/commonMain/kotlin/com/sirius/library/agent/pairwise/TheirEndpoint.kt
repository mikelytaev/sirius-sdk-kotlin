package com.sirius.library.agent.pairwise

import kotlin.jvm.JvmOverloads

open class TheirEndpoint @JvmOverloads constructor(
    var endpointAddress: String?,
    var verkey: String?,
    var routingKeys: List<String>? = null
) {

    init {
        if (routingKeys == null) {
            routingKeys = ArrayList<String>()
        }
    } /*
    @property
    def netloc(self) -> Optional[str]:
            if self.endpoint:
            return urlparse(self.endpoint).netloc
        else:
                return None

    @netloc.setter
    def netloc(self, value: str):
            if self.endpoint:
    components = list(urlparse(self.endpoint))
    components[1] = value
    self.endpoint = urlunparse(components)
*/
    /*   public String netloc(){

    }

    @property
    def netloc(self) -> Optional[str]:
            if self.endpoint:
            return urlparse(self.endpoint).netloc
        else:
                return None

    @netloc.setter
    def netloc(self, value: str):
            if self.endpoint:
    components = list(urlparse(self.endpoint))
    components[1] = value
    self.endpoint = urlunparse(components)*/
}
