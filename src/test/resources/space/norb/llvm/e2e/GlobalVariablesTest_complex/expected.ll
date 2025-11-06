@global_counter = internal global i32 0
@global_multiplier = private constant i32 10

define i32 @complex_global_ops(i32 %arg0) {
entry:
  %counter = load i32, ptr @global_counter
  %multiplier = load i32, ptr @global_multiplier
  %temp1 = add i32 %counter, %arg0
  %result = mul i32 %temp1, %multiplier
  store i32 %temp1, ptr @global_counter
  ret i32 %result
}