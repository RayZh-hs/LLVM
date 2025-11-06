define ptr @struct_in_array() {
entry:
  %struct = alloca { i32, [3 x i32], i64 }
  ret ptr %struct
}