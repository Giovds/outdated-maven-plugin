# The Outdated Maven Plugin

> Stay up-to-date and secure with The Outdated Maven Plugin!

The Outdated Maven Plugin is a tool designed to help developers identify outdated dependencies in their Maven projects.
By scanning the dependencies of your project, this plugin determines if they are no longer actively maintained
based on a user-defined threshold of inactivity in years. This ensures that your project remains up-to-date with the
latest and most secure versions of its dependencies.

## Usage

You can use the plugin as standalone for a quick check by simply running the following command in your favourite
project:\
`mvn com.giovds:outdated-maven-plugin:check -Dyears=<number_of_years>`

Or you can use the plugin to get the average and total age of all the dependencies in your project:\
`mvn com.giovds:outdated-maven-plugin:average`

Alternatively, you can integrate the plugin into your Maven project by adding the following configuration to your `pom.xml` file:
```xml

<build>
    <plugins>
        <plugin>
            <groupId>com.giovds</groupId>
            <artifactId>outdated-maven-plugin</artifactId>
            <version>1.3.0</version>
            <configuration>
                <!-- The maximum amount of inactive years allowed -->
                <years>1</years>
                <!-- Whether to fail the build if an outdated dependency is found -->
                <shouldFailBuild>false</shouldFailBuild>
            </configuration>
            <executions>
                <execution>
                    <id>outdated-check</id>
                    <goals>
                        <goal>check</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Contributing

Contributions are welcome! \
Please verify if a similar issue is not reported already. If it is not create one, if it is.

## License

This project is licensed under the MIT License - see the [LICENSE](./LICENSE) file for details.
