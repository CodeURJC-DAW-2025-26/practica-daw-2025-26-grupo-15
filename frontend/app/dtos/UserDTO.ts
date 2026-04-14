import type { ImageDTO } from "./ImageDTO";

export interface UserDTO{
    id: number;
    name: string;
    email: string;
    photo: ImageDTO | null;
    
}