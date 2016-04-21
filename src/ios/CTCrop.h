#import <Cordova/CDVPlugin.h>
#import "PECropViewController.h"
#import "ImageHelpers.h"

@interface CTCrop : CDVPlugin <PECropViewControllerDelegate>
- (void) cropImage:(CDVInvokedUrlCommand *) command;
@end
