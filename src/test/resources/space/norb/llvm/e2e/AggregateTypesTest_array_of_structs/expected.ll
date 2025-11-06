define i32 @array_of_structs(ptr %arg0, i32 %arg1) {
entry:
  %field_ptr = getelementptr [5 x { i32, i64 }], ptr %arg0, i32 0, i32 %arg1, i32 0
  %field = load i32, ptr %field_ptr
  ret i32 %field
}