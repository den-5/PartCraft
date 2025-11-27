export interface UserDTO {
    id: number;
    email: string;
    username: string;
    // add other fields as needed
}

export interface UpdateUserDTO {
    email: string;
    username: string;
    password: string;
}
