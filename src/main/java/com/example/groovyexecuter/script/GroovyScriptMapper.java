package com.example.groovyexecuter.script;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroovyScriptMapper {
    @Select("""
            SELECT id, name, description, content, execute_args_json, created_at, updated_at
            FROM groovy_script
            ORDER BY id DESC
            """)
    List<GroovyScript> findAll();

    @Select("""
            SELECT id, name, description, content, execute_args_json, created_at, updated_at
            FROM groovy_script
            WHERE id = #{id}
            """)
    GroovyScript findById(@Param("id") Long id);

    @Insert("""
            INSERT INTO groovy_script(name, description, content, execute_args_json)
            VALUES(#{name}, #{description}, #{content}, #{executeArgsJson})
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GroovyScript script);

    @Update("""
            UPDATE groovy_script
            SET name = #{name}, description = #{description}, content = #{content}, execute_args_json = #{executeArgsJson}
            WHERE id = #{id}
            """)
    int update(GroovyScript script);

    @Delete("DELETE FROM groovy_script WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
