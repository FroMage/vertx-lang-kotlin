/*
 * Copyright 2019 Red Hat, Inc.
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
package io.vertx.kotlin.ext.auth.jwt

import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.vertx.ext.auth.jose.JWTOptions
import io.vertx.ext.auth.jose.KeyStoreOptions
import io.vertx.ext.auth.jose.PubSecKeyOptions

/**
 * A function providing a DSL for building [io.vertx.ext.auth.jwt.JWTAuthOptions] objects.
 *
 * Options describing how an JWT Auth should behave.
 *
 * @param keyStore 
 * @param pubSecKeys 
 * @param jwtOptions 
 * @param jwks 
 *
 * <p/>
 * NOTE: This function has been automatically generated from the [io.vertx.ext.auth.jwt.JWTAuthOptions original] using Vert.x codegen.
 */
fun jwtAuthOptionsOf(
  keyStore: io.vertx.ext.auth.jose.KeyStoreOptions? = null,
  pubSecKeys: Iterable<io.vertx.ext.auth.jose.PubSecKeyOptions>? = null,
  jwtOptions: io.vertx.ext.auth.jose.JWTOptions? = null,
  jwks: Iterable<io.vertx.core.json.JsonObject>? = null): JWTAuthOptions = io.vertx.ext.auth.jwt.JWTAuthOptions().apply {

  if (keyStore != null) {
    this.setKeyStore(keyStore)
  }
  if (pubSecKeys != null) {
    this.setPubSecKeys(pubSecKeys.toList())
  }
  if (jwtOptions != null) {
    this.setJWTOptions(jwtOptions)
  }
  if (jwks != null) {
    this.setJwks(jwks.toList())
  }
}

