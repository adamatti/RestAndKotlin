package adamatti

import com.google.gson.GsonBuilder
import io.javalin.ApiBuilder.*
import io.javalin.Javalin
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import spark.Spark
import java.util.*

////////////////////////////////////////////////////////////////////////////////////
// https://discuss.kotlinlang.org/t/best-practices-for-loggers/226
fun <T> loggerFor(clazz: Class<T>):Logger = LoggerFactory.getLogger(clazz)

fun loggerFor(name: String):Logger = LoggerFactory.getLogger(name)

private val log = loggerFor("adamatti.main")
////////////////////////////////////////////////////////////////////////////////////
// http://kotlination.com/kotlin/kotlin-convert-object-to-from-json-gson
// http://kotlination.com/kotlin/kotlin-convert-object-to-from-json-jackson-2 (alternative)
private val gson = GsonBuilder().setPrettyPrinting().create()

////////////////////////////////////////////////////////////////////////////////////

private val personRepo = PersonRepo
private val port = 7000

fun main(args: Array<String>){
    log.info ("Starting")
    databaseSeeding()
    //registerJavalin()
    registerSpark()
    log.info("Started")
}

private fun databaseSeeding(){
    personRepo.save(Person(firstName = "Marcelo", lastName = "Adamatti"))
}

private fun registerSpark(){
    Spark.port(port)

    Spark.get("/person"){req, res ->
        res.header("Content-Type","application/json")

        gson.toJson(personRepo.list())
    }

    Spark.post("/person"){req, res ->
        res.header("Content-Type","application/json")

        val person = gson.fromJson(req.body(),Person::class.java)
        personRepo.save(person)
        req.body()
    }

    Spark.delete("/person"){req, res ->
        res.header("Content-Type","application/json")

        val person = gson.fromJson(req.body(),Person::class.java)
        personRepo.delete(person)

        res.status(204)
    }
}

private fun registerJavalin() {
    val app = Javalin.start(port)

    app.routes {
        path("/person"){
            get { ctx ->
                ctx.json(personRepo.list())
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
