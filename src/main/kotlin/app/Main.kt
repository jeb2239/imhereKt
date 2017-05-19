package app

import app.user.UserDao
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import spark.Request
import spark.Spark.*
import kotlinx.html.*
import spark.template.*;

//import kotlinx.html.dom.createHTMLDocument
import kotlinx.html.stream.appendHTML
import spark.ModelAndView
import java.util.HashMap

import com.hubspot.jinjava.*

import com.hubspot.jinjava.doc.JinjavaDoc;
import com.hubspot.jinjava.doc.JinjavaDocFactory;
import com.hubspot.jinjava.el.ExtendedSyntaxBuilder;
import com.hubspot.jinjava.el.TruthyTypeConverter;
import com.hubspot.jinjava.interpret.Context;
import com.hubspot.jinjava.interpret.FatalTemplateErrorsException;
import com.hubspot.jinjava.interpret.InterpretException;
import com.hubspot.jinjava.interpret.JinjavaInterpreter;
import com.hubspot.jinjava.interpret.RenderResult;
import com.hubspot.jinjava.interpret.TemplateError;
import com.hubspot.jinjava.interpret.TemplateError.ErrorType;
import com.hubspot.jinjava.loader.ClasspathResourceLocator;
import com.hubspot.jinjava.loader.ResourceLocator;
import spark.template.jinjava.JinjavaEngine;



fun main(args: Array<String>) {

    exception(Exception::class.java) { e, req, res -> e.printStackTrace() }

    val userDao = UserDao()
    get("/hello", { request, response ->
        val attributes = HashMap<String, Any>()
        attributes.put("message", "spark-template-jinjava")
        ModelAndView(attributes, "template\\hello.jin")
    }, JinjavaEngine())
    get("/idx") {
        request, response ->
        StringBuilder().apply( {
            appendHTML().html {
                body {
                    h1 {
                        +"header"
                    }
                    h2 {
                        +"content"

                    }
                }
            }
        }).toString()

    }

    path("/users") {


        get("") { req, res ->
            jacksonObjectMapper().writeValueAsString(userDao.users)
        }

        get("/:id") { req, res ->
            userDao.findById(req.params("id").toInt())
        }

        get("/email/:email") { req, res ->
            userDao.findByEmail(req.params("email"))
        }

        post("/create") { req, res ->
            userDao.save(name = req.qp("name"), email = req.qp("email"))
            res.status(201)
            "ok"
        }

        patch("/update/:id") { req, res ->
            userDao.update(
                    id = req.params("id").toInt(),
                    name = req.qp("name"),
                    email = req.qp("email")
            )
            "ok"
        }

        delete("/delete/:id") { req, res ->
            userDao.delete(req.params("id").toInt())
            "ok"
        }

    }

    userDao.users.forEach(::println)

}

fun Request.qp(key: String): String = this.queryParams(key) //adds .qp alias for .queryParams on Request object
