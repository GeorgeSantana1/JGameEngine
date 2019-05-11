package com.johnsproject.jpge2.processor;

public class CentralProcessor {

	private final ColorProcessor colorProcessor;
	private final GraphicsProcessor graphicsProcessor;
	private final MathProcessor mathProcessor;
	private final MatrixProcessor matrixProcessor;
	private final VectorProcessor vectorProcessor;

	public CentralProcessor() {
		this.mathProcessor = new MathProcessor();
		this.colorProcessor = new ColorProcessor(mathProcessor);
		this.matrixProcessor = new MatrixProcessor(mathProcessor);
		this.vectorProcessor = new VectorProcessor(mathProcessor);
		this.graphicsProcessor = new GraphicsProcessor(mathProcessor, matrixProcessor, vectorProcessor);
	}

	public ColorProcessor getColorProcessor() {
		return colorProcessor;
	}

	public GraphicsProcessor getGraphicsProcessor() {
		return graphicsProcessor;
	}

	public MathProcessor getMathProcessor() {
		return mathProcessor;
	}

	public MatrixProcessor getMatrixProcessor() {
		return matrixProcessor;
	}

	public VectorProcessor getVectorProcessor() {
		return vectorProcessor;
	}
}
