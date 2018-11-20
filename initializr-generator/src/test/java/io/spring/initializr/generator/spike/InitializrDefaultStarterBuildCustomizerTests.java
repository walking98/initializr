/*
 * Copyright 2012-2018 the original author or authors.
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

package io.spring.initializr.generator.spike;

import io.spring.initializr.generator.ProjectRequest;
import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.DependencyType;
import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.spike.build.InitializrDefaultStarterBuildCustomizer;
import io.spring.initializr.metadata.Dependency;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.test.metadata.InitializrMetadataTestBuilder;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link InitializrDefaultStarterBuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
public class InitializrDefaultStarterBuildCustomizerTests {

	@Test
	public void defaultStarterIsAddedIfNoneExists() {
		Dependency dependency = Dependency.withId("acme", "com.example", "acme");
		dependency.setStarter(false);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		Build build = new MavenBuild();
		build.addDependency("acme", "com.example", "acme", DependencyType.COMPILE);
		new InitializrDefaultStarterBuildCustomizer(metadata).customize(build);
		assertThat(build.getDependencies()).containsOnlyKeys("acme",
				ProjectRequest.DEFAULT_STARTER);
	}

	@Test
	public void defaultStarterIsAddedIfNoCompileScopedStarterExists() {
		Dependency dependency = Dependency.withId("runtime", "org.springframework.boot",
				"runtime-starter", null, Dependency.SCOPE_RUNTIME);
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", dependency).build();
		Build build = new MavenBuild();
		build.addDependency("runtime", "org.springframework.boot", "runtime-starter",
				DependencyType.RUNTIME);
		new InitializrDefaultStarterBuildCustomizer(metadata).customize(build);
		assertThat(build.getDependencies()).containsOnlyKeys("runtime",
				ProjectRequest.DEFAULT_STARTER);
	}

	@Test
	public void defaultStarterIsNotAddedIfCompileScopedStarterExists() {
		InitializrMetadata metadata = InitializrMetadataTestBuilder.withDefaults()
				.addDependencyGroup("test", "web", "security").build();
		Build build = new MavenBuild();
		build.addDependency("web", "org.springframework.boot", "spring-boot-starter-web",
				DependencyType.COMPILE);
		new InitializrDefaultStarterBuildCustomizer(metadata).customize(build);
		assertThat(build.getDependencies()).containsOnlyKeys("web");
	}

}