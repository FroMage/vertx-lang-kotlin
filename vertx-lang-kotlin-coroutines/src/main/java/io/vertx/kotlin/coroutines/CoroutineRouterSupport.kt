/*
 * Copyright 2023 Red Hat, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 *
 * The Eclipse Public License is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * The Apache License v2.0 is available at
 * http://www.opensource.org/licenses/apache2.0.php
 *
 * You may elect to redistribute this code under either of these licenses.
 */
package io.vertx.kotlin.coroutines

import io.vertx.core.internal.VertxInternal
import io.vertx.ext.web.Route
import io.vertx.ext.web.Router
import io.vertx.ext.web.RoutingContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * Calls the specified function [block] with a [CoroutineRouterSupport] object as its receiver.
 *
 * The receiver's scope is the [CoroutineScope] of the caller.
 */
fun CoroutineScope.coroutineRouter(block: CoroutineRouterSupport.() -> Unit) {
  val receiver = object : CoroutineRouterSupport {
    override val coroutineContext = this@coroutineRouter.coroutineContext
  }
  with(receiver) {
    block()
  }
}

/**
 * Adds support for suspending functions to the Vert.x Web [Router].
 *
 * Objects of this type implement [CoroutineScope] to define a scope for new coroutines.
 * Typically, this is the scope of a [CoroutineVerticle].
 */
interface CoroutineRouterSupport : CoroutineScope {

  /**
   * Similar to [Router.errorHandler] but using a suspending [errorHandler].
   *
   * The coroutine context is inherited from the [CoroutineScope].
   * Additional context elements can be specified with the [context] argument.
   *
   * @param context additional context elements, [EmptyCoroutineContext] by default
   */
  fun Router.coErrorHandler(
    statusCode: Int,
    context: CoroutineContext = EmptyCoroutineContext,
    errorHandler: suspend (RoutingContext) -> Unit
  ): Router =
    errorHandler(statusCode) {
      launch(it.vertx().dispatcher() + context) {
        try {
          errorHandler(it)
        } catch (t: Throwable) {
          it.fail(t)
        }
      }
    }

  /**
   * Similar to [Route.handler] but using a suspending [requestHandler].
   *
   * The coroutine context is inherited from the [CoroutineScope].
   * Additional context elements can be specified with the [context] argument.
   *
   * @param context additional context elements, [EmptyCoroutineContext] by default
   */
  fun Route.coHandler(
    context: CoroutineContext = EmptyCoroutineContext,
    requestHandler: suspend (RoutingContext) -> Unit
  ): Route = handler {
    launch(it.vertx().dispatcher() + context) {
      try {
        requestHandler(it)
      } catch (t: Throwable) {
        it.fail(t)
      }
    }
  }

  /**
   * Similar to [Route.failureHandler] but using a suspending [failureHandler].
   *
   * The coroutine context is inherited from the [CoroutineScope].
   * Additional context elements can be specified with the [context] argument.
   *
   * @param context additional context elements, [EmptyCoroutineContext] by default
   */
  fun Route.coFailureHandler(
    context: CoroutineContext = EmptyCoroutineContext,
    failureHandler: suspend (RoutingContext) -> Unit
  ): Route = failureHandler {
    launch(it.vertx().dispatcher() + context) {
      try {
        failureHandler(it)
      } catch (t: Throwable) {
        it.fail(t)
      }
    }
  }

  /**
   * Similar to [Route.respond] but using a suspending [function].
   *
   * The coroutine context is inherited from the [CoroutineScope].
   * Additional context elements can be specified with the [context] argument.
   *
   * @param context additional context elements, [EmptyCoroutineContext] by default
   */
  fun <T> Route.coRespond(
    context: CoroutineContext = EmptyCoroutineContext,
    function: suspend (RoutingContext) -> T
  ): Route = respond {
    val vertx = it.vertx() as VertxInternal
    val promise = vertx.promise<T>()
    launch(it.vertx().dispatcher() + context) {
      try {
        promise.complete(function.invoke(it))
      } catch (t: Throwable) {
        it.fail(t)
      }
    }
    promise.future()
  }
}
