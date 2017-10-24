/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.spring.deepdive

import java.time.LocalDateTime

import io.spring.deepdive.model.Post
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.web.client.HttpServerErrorException

import org.springframework.web.client.getForObject

class PostApiTests : AbstractIntegrationTests() {

    @Test
    fun `Assert findAll JSON API is parsed correctly and contains 3 elements`() {
        val posts = restTemplate.getForObject<List<Post>>("/api/post/")
        assertThat(posts).hasSize(3)
    }

    @Test
    fun `Verify findOne JSON API`() {
        val post = restTemplate.getForObject<Post>("/api/post/reactor-bismuth-is-out")!!
        assertThat(post.title).isEqualTo("Reactor Bismuth is out")
        assertThat(post.headline).startsWith("It is my great pleasure to")
        assertThat(post.content).startsWith("With the release of")
        assertThat(post.addedAt).isEqualTo(LocalDateTime.of(2017, 9, 28, 12, 0))
        assertThat(post.author.firstname).isEqualTo("Simon")
    }

    @Test
    fun `Verify findOne JSON API with Markdown converter`() {
        val post = this.restTemplate.getForObject("/api/post/reactor-bismuth-is-out?converter=markdown", Post::class.java)!!
        assertThat(post.title).startsWith("<p>Reactor Bismuth is out")
        assertThat(post.headline).doesNotContain("**3.1.0.RELEASE**").contains("<strong>3.1.0.RELEASE</strong>")
        assertThat(post.content).doesNotContain("[Spring Framework 5.0](https://spring.io/blog/2017/09/28/spring-framework-5-0-goes-ga)").contains("<a href=\"https://spring.io/blog/2017/09/28/spring-framework-5-0-goes-ga\">")
        assertThat(post.addedAt).isEqualTo(LocalDateTime.of(2017, 9, 28, 12, 0))
        assertThat(post.author.firstname).isEqualTo("Simon")
    }

    @Test
    fun `Verify findOne JSON API with invalid converter`() {
        assertThatThrownBy { this.restTemplate.getForEntity("/api/post/reactor-bismuth-is-out?converter=foo", Post::class.java) }.isInstanceOf(HttpServerErrorException::class.java)
    }

}
