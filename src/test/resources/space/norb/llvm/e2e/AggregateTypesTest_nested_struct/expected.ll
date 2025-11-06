define i32 @nested_struct(ptr %arg0) {
entry:
  %field_ptr = gep { float, { i32, i64 }, i8 }, ptr %arg0, 0, 1, 0
  %field = load i32, ptr %field_ptr
  ret i32 %field
}