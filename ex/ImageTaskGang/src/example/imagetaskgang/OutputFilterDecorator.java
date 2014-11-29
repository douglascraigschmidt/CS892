package example.imagetaskgang;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @class OutputFilterDecorator
 *
 * @brief A Decorator that applies the filter passed to its
 *        constructor and then writes the results to an output file.
 *        Plays the role of the "Concrete Decorator" in the Decorator
 *        pattern.
 */
public class OutputFilterDecorator extends FilterDecorator {
	
	/**
     * Constructs the filter decorator with the @a filter to apply
     */
    public OutputFilterDecorator(Filter filter) {
    	super(filter);
    }

    /**
     * The hook method that defines the logic for processing the
     * result by first forwarding to the super class for filtering and
     * then writing the results to an output file.
     */
	@Override
	protected InputEntity decorate(InputEntity inputEntity) {
		// Call the applyFilter() hook method.
		ImageEntity result = (ImageEntity) inputEntity;
		
        // @@ Nolan use a try with resources once we upgrade to 1.7
        try {
            // Make a directory for the filter if it does not already
            // exist.
            File externalFile = 
                new File(PlatformStrategy.instance().getDirectoryPath(),
                         this.getName());
            externalFile.mkdirs();
	        
            // We will store the filtered image as its original
            // filename, within the appropriate filter directory to
            // organize the filtered results.
            File newImage = 
                new File(externalFile, 
                         result.getFileName());
            
            // Write the compressed image to the appropriate
            // directory.
            FileOutputStream outputFile = 
                new FileOutputStream(newImage);
            PlatformStrategy.instance().storeImage(result.getImage(), 
                                                   outputFile);

            // @@ Nolan, we need to fix this once I've upgraded my
            // Android IDE to the right version of Eclipse and Java
            // 1.7.
            outputFile.close();
        } catch (Exception e) {
            System.out.println("get() exception");
        }

        return result;
	}

}
