load("@rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library")
package(default_visibility = ["//visibility:public"])


kt_jvm_library(
    name = "cellsim",
    srcs = ["Cellsim.kt"],
    deps = [
        "cellsim_base",
        "cellsim_examples",
    ],
)

kt_jvm_library(
    name = "cellsim_base",
    srcs = glob([
       "simulate/**",
       "misc/**",
    ]),
)

kt_jvm_library(
    name = "cellsim_examples",
    srcs = glob([
       "examples/**",
    ]),
    deps = [":cellsim_base"],
)