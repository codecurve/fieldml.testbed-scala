package framework.datastore

import fieldml.evaluator.Evaluator
import fieldml.valueType.ValueType

import framework.value.Value

abstract class DataDescription( valueType : ValueType )
{
    def indexEvaluators : Array[Evaluator]
    
    
    def update( indexes : Array[Int], value : Value )
    
    
    def update( indexes : Array[Int], value : Int )
    {
        update( indexes, Value( valueType, value ) )
    }
    
    
    def update( index1 : Int, value : Int )
    {
        update( Array[Int]( index1 ), Value( valueType, value ) )
    }
    
    
    def update( index1 : Int, index2 : Int, value : Int )
    {
        update( Array[Int]( index1, index2 ), Value( valueType, value ) )
    }
    
    
    def update( indexes : Array[Int], values : Double* )
    {
        update( indexes, Value( valueType, values: _* ) )
    }
    
    
    def update( index1 : Int, values : Double* )
    {
        update( Array[Int]( index1 ), Value( valueType, values: _* ) )
    }
    
    
    def update( index1 : Int, index2 : Int, values : Double* )
    {
        update( Array[Int]( index1, index2 ), Value( valueType, values: _* ) )
    }
    
    
    def apply( indexes : Array[Int] ) : Option[Value]
    
    
    def apply( index1 : Int ) : Option[Value] =
    {
        return apply( Array[Int]( index1 ) )
    }
    
    
    def apply( index1 : Int, index2 : Int ) : Option[Value] =
    {
        return apply( Array[Int]( index1, index2 ) )
    }
}
