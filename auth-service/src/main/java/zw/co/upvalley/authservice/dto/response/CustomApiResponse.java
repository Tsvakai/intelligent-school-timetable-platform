package zw.co.upvalley.authservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Generic API response wrapper")
public class CustomApiResponse<T> {

    @Schema(description = "Response status", example = "success")
    private String status;

    @Schema(description = "Response message", example = "Operation completed successfully")
    private String message;

    @Schema(description = "Response data")
    private T data;

    @Schema(description = "Timestamp in milliseconds")
    private Long timestamp;

    // Success static methods
    public static <T> CustomApiResponse<T> success(String message, T data) {
        return CustomApiResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    public static CustomApiResponse<Void> success(String message) {
        return success(message, null);
    }

    // Error static methods
    public static <T> CustomApiResponse<T> error(String message) {
        return CustomApiResponse.<T>builder()
                .status("error")
                .message(message)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}