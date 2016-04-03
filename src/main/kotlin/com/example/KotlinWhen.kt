package com.example

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

/*
Leaving it all in one file so that it is easier to see all of the moving parts
in one place.

You could break this up into separate files in a separate package for "production ready"
code so that it would be easier to see the parts.
 */

// Application layer
@Configuration
open class JacksonKotlinConfig {
    @Bean
    open fun objectMapperBuilder() = Jackson2ObjectMapperBuilder().modulesToInstall(KotlinModule())
}

@RestController
class KotlinWhen(val fooService: FooService) {

    @RequestMapping(value = "/kotlinWhen", method = arrayOf(RequestMethod.POST))
    fun createFoo(@RequestBody foo: FooRequest): ResponseEntity<CreateFooResponse> {
        val result = this.fooService.createFoo(foo.name)

        return when (result) {
            is CreateFooResponse.Success -> ResponseEntity(result, HttpStatus.CREATED)
            is CreateFooResponse.InvalidObject -> ResponseEntity(result, HttpStatus.UNPROCESSABLE_ENTITY)
        }
    }
}

// You need this class because the createdAt field on the Foo domain object
// is non-nullable. Spring can not deserialize the request body without it.
// Kotlin makes it so easy to create these classes that it is almost silly
// to not create them from the start.
data class FooRequest(val name: String)

// This exists because we didn't want the json to look like "foo":{}
// This also gives you a layer of indirection between your domain and
// the the outside world. This layer of indirection comes in handy if you
// want the JSON to be different than the DB column names or if you
// don't want to expose all of the fields.
class SuccessCreateFooResponse : JsonSerializer<CreateFooResponse.Success>() {
    override fun serialize(value: CreateFooResponse.Success, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeStartObject()
        gen.writeNumberField("id", value.foo.id)
        gen.writeStringField("createdAt", value.foo.createdAt.toString())
        gen.writeEndObject()
    }
}
// end of application layer

// Domain layer
sealed class CreateFooResponse {
    // Look into how to move this reference to the Application layer (SuccessCreateFooResponse)
    // back into the application layer using something like this:
    /*
Item myItem = new Item(1, "theItem", new User(2, "theUser"));
ObjectMapper mapper = new ObjectMapper();

SimpleModule module = new SimpleModule();
module.addSerializer(Item.class, new ItemSerializer());
mapper.registerModule(module);

String serialized = mapper.writeValueAsString(myItem);
     */
    @JsonSerialize(using = SuccessCreateFooResponse::class)
    class Success(val foo: Foo) : CreateFooResponse()

    class InvalidObject(val errors: Array<String>) : CreateFooResponse()
}

@Service
class FooService() {
    fun createFoo(name: String): CreateFooResponse {
        val foo = Foo(1, name, LocalDate.of(2016, 2, 2))

        return if (foo.name == "badName") {
            CreateFooResponse.InvalidObject(arrayOf("InvalidName"))
        } else {
            CreateFooResponse.Success(foo)
        }
    }
}

data class Foo(val id: Int, val name: String, val createdAt: LocalDate)

// End domain layer
