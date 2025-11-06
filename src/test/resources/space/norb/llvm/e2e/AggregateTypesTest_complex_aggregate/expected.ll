define i64 @complex_aggregate_ops(ptr %arg0, i32 %arg1, i32 %arg2) {
entry:
  %field_ptr = gep [10 x { i32, i64 }], ptr %arg0, 0, %arg1, %arg2
  %field = load i64, ptr %field_ptr
  %result = add i64 %field, 100
  ret i64 %result
}