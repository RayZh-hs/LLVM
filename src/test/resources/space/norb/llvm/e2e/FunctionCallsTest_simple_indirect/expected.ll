define i32 @subtract(i32 %a, i32 %b) {
entry:
  %result = sub i32 %a, %b
  ret i32 %result
}

define i32 @main() {
entry:
  %func_ptr = bitcast i32 (i32, i32)* @subtract to ptr
  %call_result = call i32 %func_ptr(i32 10, i32 3)
  ret i32 %call_result
}