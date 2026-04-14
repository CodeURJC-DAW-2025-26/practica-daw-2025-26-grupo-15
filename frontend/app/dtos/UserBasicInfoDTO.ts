import type { ImageDto } from "./ImageDto";

export default interface UserBasicInfoDTO {
    id: number;
    name: string;
    email: string;
    photo: ImageDto;
}