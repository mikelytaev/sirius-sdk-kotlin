#import <Foundation/Foundation.h>
#import "IndyTypes.h"

@interface IndyUtils : NSObject

+ (void)setRuntimeConfig:(NSString *)config;

+ (void)testGlobalSync:(NSString *)config
            completion:(void (^)(NSString *key))completion;

+ (void)testGlobalAsync:(NSString *)config
             completion:(void (^)(NSString *key))completion;

+ (void)testMainAsync:(NSString *)config;

+ (void)testMainSync:(NSString *)config;



@end
