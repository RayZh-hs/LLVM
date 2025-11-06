define i32 @nested_struct(ptr %arg0) {
entry:
  %field_ptr = getelementptr { float, { i32, i64 }, i8 }, ptr %arg0, i32 0, i32 1, i32 0
  %field = load i32, ptr %field_ptr
  ret i32 %field
}