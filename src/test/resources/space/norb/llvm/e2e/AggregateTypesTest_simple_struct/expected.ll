define ptr @simple_struct() {
entry:
  %struct = alloca { i32, i64, float }
  ret ptr %struct
}