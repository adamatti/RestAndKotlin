package adamatti

import java.util.*

object PersonRepo {
    private val log = loggerFor(this.javaClass)
    private val memory = mutableMapOf<String?,Person>()

    fun save(person: Person): Person {
        log.info("Save called: ${person}")
        if (person.id == null){
            person.id = UUID.randomUUID().toString()
        }
        memory.put(person.id,person)
        return person
    }

    fun list() = memory.values

    fun delete(person:Person) = memory.remove(person.id)
}

data class Person (
    var id:String?=null,
    var firstName: String?=null,
    var lastName: String?=null
)
