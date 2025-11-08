; Module: MultipleStructsOrderTest

%AStruct = type { i32 }
%MStruct = type { i16 }
%ZStruct = type { i64 }

define i32 @processAllStructs(%AStruct %arg0, %MStruct %arg1, %ZStruct %arg2) {
entry:
  ret i32 42
}