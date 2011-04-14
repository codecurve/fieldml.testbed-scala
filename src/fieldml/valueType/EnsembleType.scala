package fieldml.valueType

import fieldml.FieldmlObject
import fieldml.ElementSet

class EnsembleType( name : String, val isComponent : Boolean )
    extends ValueType( name )
{
    val elementSet = new ElementSet( name + ".elements", this )
    
    def elementCount = elementSet.size
}
