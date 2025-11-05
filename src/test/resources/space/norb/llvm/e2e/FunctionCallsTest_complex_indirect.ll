define i32 @add(i32 %a, i32 %b) {
entry:
  %result = add i32 %a, %b
  ret i32 %result
}

define i32 @multiply(i32 %a, i32 %b) {
entry:
  %result = mul i32 %a, %b
  ret i32 %result
}

define i32 @main() {
entry:
  %condition = icmp eq i32 1, 0
  br i1 %condition, label %use_mul, label %use_add

use_add:
  %func_ptr = bitcast i32 (i32, i32)* @add to ptr
  %call_result = call i32 %func_ptr(i32 5, i32 3)
  br label %merge

use_mul:
  %func_ptr1 = bitcast i32 (i32, i32)* @multiply to ptr
  %call_result2 = call i32 %func_ptr1(i32 4, i32 6)
  br label %merge

merge:
  %result = phi i32 [ %call_result, %use_add ], [ %call_result2, %use_mul ]
  ret i32 %result
}