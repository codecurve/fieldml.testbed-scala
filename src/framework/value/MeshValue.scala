package framework.value

import fieldml.valueType.MeshType

class MeshValue( vType : MeshType, val elementValue : Int, val xiValue : Array[Double] )
    extends StructuredValue( vType,
        Tuple2( "element", new EnsembleValue( vType.elementType, elementValue ) ),
        Tuple2( "xi", new ContinuousValue( vType.xiType, xiValue ) )
        )
{
    def this( vType : MeshType, elementValue : Int, xiValue : Double* ) =
    {
        this( vType, elementValue, xiValue.toArray )
    }


    override def toString() : String =
    {
        val string = new StringBuilder
        string.append( "[ " )
        string.append( elementValue )
        string.append( " " )
        for( v <- xiValue )
        {
            string.append( v )
            string.append( " " )
        }
        string.append( "]" )
        
        return string.toString
    }
}
