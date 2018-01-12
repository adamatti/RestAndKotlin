package adamatti

import io.javalin.ApiBuilder.*
import io.javalin.Javalin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

// https://discuss.kotlinlang.org/t/best-practices-for-loggers/226
fun <T> loggerFor(clazz: Class<T>):Logger = LoggerFactory.getLogger(clazz)

fun loggerFor(name: String):Logger = LoggerFactory.getLogger(name)

private val log = loggerFor("adamatti.main")
private val personRepo = PersonRepo

fun main(args: Array<String>){
    log.info ("Starting")
    databaseSeeding()
    registerJavalin()
    log.info("Started")
}

private fun databaseSeeding(){
    personRepo.save(Person(firstName = "Marcelo", lastName = "Adamatti"))
}

private fun registerJavalin() {
    val app = Javalin.start(7000)

    app.routes {
        path("/person"){
            get { ctx ->
                ctx.json(PersonRepo.list())
            }
            post { ctx ->
                val person = ctx.bodyAsClass(Person::class.java)
                personRepo.save(person)
                ctx.json(person)
            }

            delete { ctx ->
                val person = ctx.bodyAsClass(Person::class.java)
                personRepo.delete(person)
                ctx.status(204)
            }
        }
    }

    app.get("/healthcheck"){ctx ->
        ctx.json(mapOf(
            "status" to "Ok",
            "date" to Date().toString()
        ))
    }
}
