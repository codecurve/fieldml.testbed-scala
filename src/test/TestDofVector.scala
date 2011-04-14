package test

import fieldml.jni.FieldmlApi._
import fieldml.jni.FieldmlApiConstants._
import fieldml.jni.DataDescriptionType._
import fieldml.jni.DataSourceType._

object TestDofVector 
{
    private def exportExample1() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_1" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetEnsembleElementRange( fml, nodesType, 1, 8, 1 )
        
        val nodesVariable = Fieldml_CreateAbstractEvaluator( fml, "test.mesh_nodes.variable", nodesType )
        
        val bilinearNodesVariable = Fieldml_GetObjectByName( fml, "library.localNodes.2d.square2x2.variable" )
        
        val xiEnsemble = Fieldml_GetObjectByName( fml, "library.ensemble.xi.2d" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh", xiEnsemble )
        val elementsType = Fieldml_GetMeshElementType( fml, meshType )
        val xiType = Fieldml_GetMeshXiType( fml, meshType )
        Fieldml_SetMeshDefaultShape( fml, meshType, "library.shape.square" )
        Fieldml_SetEnsembleElementRange( fml, meshType, 1, 3, 1 )
        
        val meshVariable = Fieldml_CreateAbstractEvaluator( fml, "test.mesh.variable", meshType )
        val elementsVariable = Fieldml_GetObjectByName( fml, "test.mesh.variable.element" )
        val xiVariable = Fieldml_GetObjectByName( fml, "test.mesh.variable.xi" )

        val connectivity = Fieldml_CreateParametersEvaluator( fml, "test.bilinear_connectivity", nodesType )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, bilinearNodesVariable, FML_INVALID_HANDLE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, elementsVariable, FML_INVALID_HANDLE )
        
        val connectivityData = Fieldml_CreateDataObject( fml, "test.bilinear_connectivity.data" )
        Fieldml_SetDataObjectSourceType( fml, connectivityData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, connectivityData, 3, 4, 0, 0 )
        Fieldml_SetDataObject( fml, connectivity, connectivityData )

        Fieldml_AddInlineData( fml, connectivityData, "\n", 1 )
        Fieldml_AddInlineData( fml, connectivityData, "1 2 5 6\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "2 3 6 7\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "3 4 7 8", 8 );
        
        val fieldValue = Fieldml_GetObjectByName( fml, "library.real.1d" )
        val nodalParams = Fieldml_CreateParametersEvaluator( fml, "test.nodal_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, nodalParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSparseIndexEvaluator( fml, nodalParams, nodesVariable )

        val nodalData = Fieldml_CreateDataObject( fml, "test.nodal_params.data" )
        Fieldml_SetDataObjectSourceType( fml, nodalData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, nodalData, 4, 2, 0, 0 )
        Fieldml_SetDataObject( fml, nodalParams, nodalData )

        Fieldml_AddInlineData( fml, nodalData, "1 0.0 ", 6 )
        Fieldml_AddInlineData( fml, nodalData, "2 0.5 ", 6 )
        Fieldml_AddInlineData( fml, nodalData, "3 1.0 ", 6 )
        Fieldml_AddInlineData( fml, nodalData, "4 1.5 ", 6 )
        
        val elementParams = Fieldml_CreateParametersEvaluator( fml, "test.element_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, elementParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSparseIndexEvaluator( fml, elementParams, elementsVariable )

        val elementData = Fieldml_CreateDataObject( fml, "test.element_params.data" )
        Fieldml_SetDataObjectSourceType( fml, elementData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, elementData, 1, 2, 0, 0 )
        Fieldml_SetDataObject( fml, elementParams, elementData )

        Fieldml_AddInlineData( fml, elementData, "2 2.0 ", 6 )

        val globalParams = Fieldml_CreateParametersEvaluator( fml, "test.global_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, globalParams, DESCRIPTION_SEMIDENSE )

        val globalData = Fieldml_CreateDataObject( fml, "test.global_params.data" )
        Fieldml_SetDataObjectSourceType( fml, globalData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, globalData, 1, 1, 0, 0 )
        Fieldml_SetDataObject( fml, globalParams, globalData )

        Fieldml_AddInlineData( fml, globalData, "3.0 ", 4 )
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "library.interpolator.2d.unit.bilinearLagrange" )
        val bilinearEnsembleVariable = Fieldml_GetObjectByName( fml, "library.interpolator.2d.unit.bilinearLagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "library.parameters.2d.bilinearLagrange" )
        val bilinearParametersVariable = Fieldml_GetObjectByName( fml, "library.parameters.2d.bilinearLagrange.variable" )
        val generic2d = Fieldml_GetObjectByName( fml, "library.xi.2d.variable" )
        
        val bilinearNodalParams = Fieldml_CreateAggregateEvaluator( fml, "test.bilinear_nodal_params", bilinearParameters )
        Fieldml_SetIndexEvaluator( fml, bilinearNodalParams, 1, bilinearNodesVariable )
        Fieldml_SetBind( fml, bilinearNodalParams, nodesVariable, connectivity )
        Fieldml_SetDefaultEvaluator( fml, bilinearNodalParams, nodalParams )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiVariable )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParametersVariable, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsVariable )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

        Fieldml_WriteFile( fml, "test_example1.xml" )
    }

    
    private def exportExample2() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_2" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetEnsembleElementRange( fml, nodesType, 1, 8, 1 )
        
        val bilinearNodesVariable = Fieldml_GetObjectByName( fml, "library.localNodes.2d.square2x2.variable" )
        
        val xiEnsemble = Fieldml_GetObjectByName( fml, "library.ensemble.xi.2d" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh", xiEnsemble )
        Fieldml_SetMeshDefaultShape( fml, meshType, "library.shape.square" )
        Fieldml_SetEnsembleElementRange( fml, meshType, 1, 3, 1 )

        val elementsType = Fieldml_GetMeshElementType( fml, meshType )
        val xiType = Fieldml_GetMeshXiType( fml, meshType )

        val connectivity = Fieldml_CreateParametersEvaluator( fml, "test.bilinear_connectivity", nodesType )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, bilinearNodesVariable, FML_INVALID_HANDLE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, elementsType, FML_INVALID_HANDLE )
        
        val connectivityData = Fieldml_CreateDataObject( fml, "test.bilinear_connectivity.data" )
        Fieldml_SetDataObjectSourceType( fml, connectivityData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, connectivityData, 3, 4, 0, 0 )
        Fieldml_SetDataObject( fml, connectivity, connectivityData )

        Fieldml_AddInlineData( fml, connectivityData, "\n", 1 )
        Fieldml_AddInlineData( fml, connectivityData, "1 2 5 6\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "2 3 6 7\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "3 4 7 8", 8 );
        
        val dofIndexType = Fieldml_CreateEnsembleType( fml, "test.dof_number", FML_INVALID_HANDLE )
        Fieldml_SetEnsembleElementRange( fml, dofIndexType, 1, 6, 1 )
        
        val fieldValue = Fieldml_GetObjectByName( fml, "library.real.1d" )

        val dofParams = Fieldml_CreateParametersEvaluator( fml, "test.dof_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, dofParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddDenseIndexEvaluator( fml, dofParams, dofIndexType, FML_INVALID_HANDLE )
        
        val dofData = Fieldml_CreateDataObject( fml, "test.dof_params.data" )
        Fieldml_SetDataObjectSourceType( fml, dofData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, dofData, 6, 1, 0, 0 )
        Fieldml_SetDataObject( fml, dofParams, dofData )

        Fieldml_AddInlineData( fml, dofData, "0.0 0.5 1.0 1.5 2.0 3.0 ", 24 )
        
        val nodalIndexes = Fieldml_CreateParametersEvaluator( fml, "test.nodal_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, nodalIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSparseIndexEvaluator( fml, nodalIndexes, nodesType )
        
        val nodalIndexData = Fieldml_CreateDataObject( fml, "test.nodal_indexes.data" )
        Fieldml_SetDataObjectSourceType( fml, nodalIndexData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, nodalIndexData, 4, 2, 0, 0 )
        Fieldml_SetDataObject( fml, nodalIndexes, nodalIndexData )

        Fieldml_AddInlineData( fml, nodalIndexData, "1 2 ", 4 )
        Fieldml_AddInlineData( fml, nodalIndexData, "2 3 ", 4 )
        Fieldml_AddInlineData( fml, nodalIndexData, "3 4 ", 4 )
        Fieldml_AddInlineData( fml, nodalIndexData, "4 5 ", 4 )
        
        val elementIndexes = Fieldml_CreateParametersEvaluator( fml, "test.element_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, elementIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSparseIndexEvaluator( fml, elementIndexes, elementsType )
        
        val elementIndexData = Fieldml_CreateDataObject( fml, "test.element_indexes.data" )
        Fieldml_SetDataObjectSourceType( fml, elementIndexData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, elementIndexData, 1, 2, 0, 0 )
        Fieldml_SetDataObject( fml, elementIndexes, elementIndexData )

        Fieldml_AddInlineData( fml, elementIndexData, "2 6 ", 4 )

        val globalIndexes = Fieldml_CreateParametersEvaluator( fml, "test.global_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, globalIndexes, DESCRIPTION_SEMIDENSE )
        
        val globalIndexData = Fieldml_CreateDataObject( fml, "test.global_indexes.data" )
        Fieldml_SetDataObjectSourceType( fml, globalIndexData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, globalIndexData, 1, 1, 0, 0 )
        Fieldml_SetDataObject( fml, globalIndexes, globalIndexData )

        Fieldml_AddInlineData( fml, globalIndexData, "1 ", 2 )
        
        val nodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.nodal_params", dofParams )
        Fieldml_SetBind( fml, nodalParams, dofIndexType, nodalIndexes )

        val elementParams = Fieldml_CreateReferenceEvaluator( fml, "test.element_params", dofParams )
        Fieldml_SetBind( fml, elementParams, dofIndexType, elementIndexes )

        val globalParams = Fieldml_CreateReferenceEvaluator( fml, "test.global_params", dofParams )
        Fieldml_SetBind( fml, globalParams, dofIndexType, globalIndexes )

        val bilinearNodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_nodal_params", nodalParams )
        Fieldml_SetBind( fml, bilinearNodalParams, nodesType, connectivity )
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "library.interpolator.2d.unit.bilinearLagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "library.parameters.2d.bilinearLagrange" )
        val generic2d = Fieldml_GetObjectByName( fml, "library.xi.2d.variable" )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiType )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsType )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

//        Fieldml_WriteFile( fml, "test_example2.xml" )
    }

    
    private def exportExample3() : Unit =
    {
        val fml = Fieldml_Create( "", "dof_example_3" )
        
        val nodesType = Fieldml_CreateEnsembleType( fml, "test.mesh_nodes", FML_INVALID_HANDLE )
        Fieldml_SetEnsembleElementRange( fml, nodesType, 1, 8, 1 )
        
        val bilinearNodesVariable = Fieldml_GetObjectByName( fml, "library.localNodes.2d.square2x2.variable" )
        
        val xiEnsemble = Fieldml_GetObjectByName( fml, "library.ensemble.xi.2d" )
        
        val meshType = Fieldml_CreateMeshType( fml, "test.mesh", xiEnsemble )
        Fieldml_SetMeshDefaultShape( fml, meshType, "library.shape.square" )
        Fieldml_SetEnsembleElementRange( fml, meshType, 1, 3, 1 )

        val elementsType = Fieldml_GetMeshElementType( fml, meshType )
        val xiType = Fieldml_GetMeshXiType( fml, meshType )

        val connectivity = Fieldml_CreateParametersEvaluator( fml, "test.bilinear_connectivity", nodesType )
        
        Fieldml_SetParameterDataDescription( fml, connectivity, DESCRIPTION_SEMIDENSE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, bilinearNodesVariable, FML_INVALID_HANDLE )
        Fieldml_AddDenseIndexEvaluator( fml, connectivity, elementsType, FML_INVALID_HANDLE )
        
        val connectivityData = Fieldml_CreateDataObject( fml, "test.bilinear_connectivity.data" )
        Fieldml_SetDataObjectSourceType( fml, connectivityData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, connectivityData, 3, 4, 0, 0 )
        Fieldml_SetDataObject( fml, connectivity, connectivityData )

        Fieldml_AddInlineData( fml, connectivityData, "\n", 1 )
        Fieldml_AddInlineData( fml, connectivityData, "1 2 5 6\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "2 3 6 7\n", 8 );
        Fieldml_AddInlineData( fml, connectivityData, "3 4 7 8", 8 );

        val dofIndexType = Fieldml_CreateEnsembleType( fml, "test.dof_number", FML_INVALID_HANDLE )
        Fieldml_SetEnsembleElementRange( fml, dofIndexType, 1, 6, 1 )
        
        val fieldValue = Fieldml_GetObjectByName( fml, "library.real.1d" )

        val dofParams = Fieldml_CreateParametersEvaluator( fml, "test.dof_params", fieldValue )
        Fieldml_SetParameterDataDescription( fml, dofParams, DESCRIPTION_SEMIDENSE )
        Fieldml_AddDenseIndexEvaluator( fml, dofParams, dofIndexType, FML_INVALID_HANDLE )
        
        val dofData = Fieldml_CreateDataObject( fml, "test.dof_params.data" )
        Fieldml_SetDataObjectSourceType( fml, dofData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, dofData, 6, 1, 0, 0 )
        Fieldml_SetDataObject( fml, dofParams, dofData )

        Fieldml_AddInlineData( fml, dofData, "0.0 0.5 1.0 1.5 2.0 3.0 ", 24 )

        val dofTypeType = Fieldml_CreateEnsembleType( fml, "test.dof_type", FML_INVALID_HANDLE )
        Fieldml_SetEnsembleElementRange( fml, dofTypeType, 1, 3, 1 )
        
        val dofIndexes = Fieldml_CreateParametersEvaluator( fml, "test.dof_indexes", dofIndexType )
        Fieldml_SetParameterDataDescription( fml, dofIndexes, DESCRIPTION_SEMIDENSE )
        Fieldml_AddSparseIndexEvaluator( fml, dofIndexes, dofTypeType )
        Fieldml_AddSparseIndexEvaluator( fml, dofIndexes, nodesType )
        Fieldml_AddSparseIndexEvaluator( fml, dofIndexes, elementsType )
        
        val dofIndexData = Fieldml_CreateDataObject( fml, "test.dof_indexes.data" )
        Fieldml_SetDataObjectSourceType( fml, dofIndexData, SOURCE_INLINE )
        Fieldml_SetDataObjectEntryInfo( fml, dofIndexData, 44, 4, 0, 0 )
        Fieldml_SetDataObject( fml, dofIndexes, dofIndexData )

        Fieldml_AddInlineData( fml, dofIndexData, "\n", 1 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 1 1 2\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 1 2 2\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 1 3 2\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 2 1 3\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 2 2 3\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 2 3 3\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 3 1 4\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 3 2 4\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 3 3 4\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 4 1 5\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 4 2 5\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "1 4 3 5\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "\n", 1 )
        Fieldml_AddInlineData( fml, dofIndexData, "2 1 2 6\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "2 2 2 6\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "2 3 2 6\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "2 4 2 6\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "2 5 2 6\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "2 6 2 6\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "2 7 2 6\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "2 8 2 6\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "\n", 1 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 1 1 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 2 1 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 3 1 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 4 1 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 5 1 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 6 1 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 7 1 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 8 1 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 1 2 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 2 2 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 3 2 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 4 2 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 5 2 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 6 2 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 7 2 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 8 2 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 1 3 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 2 3 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 3 3 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 4 3 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 5 3 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 6 3 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 7 3 1\n", 8 )
        Fieldml_AddInlineData( fml, dofIndexData, "3 8 3 1", 8 )
        
        val dummyConst1 = Fieldml_CreateContinuousType( fml, "1", FML_INVALID_HANDLE )
        val dummyConst2 = Fieldml_CreateContinuousType( fml, "2", FML_INVALID_HANDLE )
        val dummyConst3 = Fieldml_CreateContinuousType( fml, "3", FML_INVALID_HANDLE )
        
        val nodalIndexes = Fieldml_CreateReferenceEvaluator( fml, "test.nodal_indexes", dofIndexes )
        Fieldml_SetBind( fml, nodalIndexes, dofTypeType, dummyConst1 )
        Fieldml_SetBind( fml, nodalIndexes, elementsType, dummyConst1 )

        val elementIndexes = Fieldml_CreateReferenceEvaluator( fml, "test.element_indexes", dofIndexes )
        Fieldml_SetBind( fml, elementIndexes, dofTypeType, dummyConst2 )
        Fieldml_SetBind( fml, elementIndexes, nodesType, dummyConst1 )

        val globalIndexes = Fieldml_CreateReferenceEvaluator( fml, "test.global_indexes", dofIndexes )
        Fieldml_SetBind( fml, globalIndexes, dofTypeType, dummyConst3 )
        Fieldml_SetBind( fml, globalIndexes, nodesType, dummyConst1 )
        Fieldml_SetBind( fml, globalIndexes, elementsType, dummyConst1 )
        
        val nodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.nodal_params", dofParams )
        Fieldml_SetBind( fml, nodalParams, dofIndexType, nodalIndexes )

        val elementParams = Fieldml_CreateReferenceEvaluator( fml, "test.element_params", dofParams )
        Fieldml_SetBind( fml, elementParams, dofIndexType, elementIndexes )

        val globalParams = Fieldml_CreateReferenceEvaluator( fml, "test.global_params", dofParams )
        Fieldml_SetBind( fml, globalParams, dofIndexType, globalIndexes )

        val bilinearNodalParams = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_nodal_params", nodalParams )
        Fieldml_SetBind( fml, bilinearNodalParams, nodesType, connectivity )
        
        val bilinearEvaluator = Fieldml_GetObjectByName( fml, "library.interpolator.2d.unit.bilinearLagrange" )
        val bilinearParameters = Fieldml_GetObjectByName( fml, "library.parameters.2d.bilinearLagrange" )
        val generic2d = Fieldml_GetObjectByName( fml, "library.xi.2d.variable" )
        
        val bilinearInterpolator = Fieldml_CreateReferenceEvaluator( fml, "test.bilinear_interpolator", bilinearEvaluator )
        Fieldml_SetBind( fml, bilinearInterpolator, generic2d, xiType )
        Fieldml_SetBind( fml, bilinearInterpolator, bilinearParameters, bilinearNodalParams )
        
        val fieldEvaluator = Fieldml_CreatePiecewiseEvaluator( fml, "test.field", fieldValue )
        Fieldml_SetIndexEvaluator( fml, fieldEvaluator, 1, elementsType )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 1, bilinearInterpolator )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 2, elementParams )
        Fieldml_SetEvaluator( fml, fieldEvaluator, 3, globalParams )

//        Fieldml_WriteFile( fml, "test_example3.xml" )
    }

    
    def main( args: Array[String] ): Unit =
    {
        exportExample1()
        
        exportExample2()
        
        exportExample3()
    }
}