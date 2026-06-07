<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('user_rewards_claimed', function (Blueprint $table) {
            $table->id();
            $table->unsignedBigInteger('user_id');
            $table->unsignedBigInteger('event_id');
            $table->unsignedBigInteger('event_reward_id');
            $table->string('reward_type', 50);
            $table->integer('reward_amount');
            $table->json('reward_data')->nullable();
            $table->dateTime('claimed_at')->useCurrent();
            $table->dateTime('processed_at')->nullable();
            $table->enum('status', ['pending', 'processed', 'failed'])->default('pending');

            $table->index('user_id');
            $table->index('event_id');
            $table->index('status');
            $table->foreign('user_id')->references('id')->on('users')->onDelete('cascade');
            $table->foreign('event_id')->references('id')->on('events')->onDelete('cascade');
            $table->foreign('event_reward_id')->references('id')->on('event_rewards')->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('user_rewards_claimed');
    }
};
