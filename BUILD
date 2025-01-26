load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library", "kt_jvm_binary")

kt_jvm_library(
    name = "demo",
    srcs = ["src/main/kotlin/Demo.kt"],
    deps = ["//src/main/kotlin/cellsim"],
)

kt_jvm_binary(
    name = "Demo",
    runtime_deps = [":demo"],
    main_class = "main.kotlin.DemoKt"
)
