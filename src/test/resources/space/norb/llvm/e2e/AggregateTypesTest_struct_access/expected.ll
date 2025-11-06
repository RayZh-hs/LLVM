define i64 @struct_access(ptr %arg0) {
entry:
  %field_ptr = getelementptr { i32, i64, float }, ptr %arg0, i32 0, i32 1
  %field = load i64, ptr %field_ptr
  ret i64 %field
}