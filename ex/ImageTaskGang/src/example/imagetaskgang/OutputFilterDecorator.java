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
     * The ImageEntity that is being filtered and output.
     */
    private ImageEntity mImageEntity;

    /**
     * Constructs the filter with the @a filter to apply and the @a
     * imageEntity the filter is being applied to.
     */
    public OutputFilterDecorator(Filter filter,
                                 ImageEntity imageEntity) {
        // Initialize the superclass.
        super(filter);
        
        // Store the imageEntity in the data member.
        mImageEntity = imageEntity;
    }

    /**
     * The hook method that defines the logic for processing the
     * result by first forwarding to the super class for filtering and
     * then writing the results to an output file.
     */
    public InputEntity applyFilter(InputEntity inputEntity) {
        // Call the applyFilter() hook method.
        InputEntity result = super.filter(inputEntity);

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
                         mImageEntity.getFileName());
	
            // Write the compressed image to the appropriate
            // directory.
            FileOutputStream outputFile = 
                new FileOutputStream(newImage);
            PlatformStrategy.instance().storeImage(mImageEntity.getImage(), 
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
