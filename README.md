# Tamagotchi simulator

A basic implementation of a [Tamagochi Simulator](https://gist.github.com/davidvuong/90f8ac0916dd3e14fad014bc814614ff/) built using Scala and Akka Actors.

## Instructions to run

The project can be built using a recent version of `sbt`.

To build it including unit tests run `sbt test` in the project directory.

To start the app run `sbt` and once the console loads run the `run` task.

Available commands:
* `activate <name>` : creates a new pet with this name
* `feed meal` : feeds the pet with a nutritious meal
* `feed snack` : feed the pet with a tasty snack
* `sleep` : puts the pet to sleep
* `exit` - exits the program (maybe)
