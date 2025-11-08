; Module: PackedStructEmissionTest

%PackedData = type <{ i8, i32, i8 }>

define %PackedData @processPackedData(%PackedData %arg0) {
entry:
  ret %PackedData %arg0
}